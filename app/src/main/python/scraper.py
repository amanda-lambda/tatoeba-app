import re
import math
import unicodedata
import requests
from bs4 import BeautifulSoup
from typing import Dict, List

# Limited for now because I'm lazy
tatoeba_language_codes = {
    '🇫🇷 French': 'fra',
    '🇩🇪 German': 'deu',
    '🇪🇸 Spanish': 'spa',
    '🇨🇳 Chinese': 'cmn',
    '🇯🇵 Japanese': 'jpn',
    '🇵🇭 Tagalog': 'tgl',
    '🇸🇦 Arabic': 'ara',
    '🇷🇺 Russian': 'rus',
    '🇰🇷 Korean': 'kor',
    '🇮🇳 Hindi': 'hin',
    '🇬🇧 English': 'eng',
    '🇻🇳 Vietnamese': 'vie',
}


def normalize(x):
    # x = x.replace('\\\\', '\\')
    y = unicodedata.normalize('NFKD', x)
    # print(x.decode('utf-8'), y.decode('utf-8'))
    return y


class TatoebaScraper():
    def __init__(self, query: str, from_lang: str, to_lang: str) -> None:
        '''
        Web scraper for Tateoba website.
        Used to find example sentences in another language.

        Example:
            We would like to find example sentences with the Vietnamese word for
            dog. We submit the following query: "dog", from_lang: "English",
            to_lang: "Vietnamese". We may get the following SearchResult:
                There is a dog on the bridge.
                Trên cây cầu có một con chó.

        Parameters
        ----------
        query: str
            Word or phrase (space separated) in `from_lang` to find example
            sentences for in the `to_lang`.
        from_lang: str
            The origin language of the `query`. See `tatoeba_language_codes` key
            values to see supported languages.
        to_lang: str
            The destination language of the returned example sentences.
        '''
        # Parse input
        self.to_lang_code = tatoeba_language_codes[to_lang]
        self.from_lang_code = tatoeba_language_codes[from_lang]
        self.query = query.replace(" ", "+") if " " in query else query

        # Instantiate page counter and search result counters
        self.page = 1 # Current Tatoeba search results page
        self.num_pages = -1 # Number of search result pages
        self.n = 1 # Current returned Tatoeba search result
        self.num_results = -1 # Number of search result pages
        self.results = [] # Our current list of fetched Tatoeba results

        self.scrape()


    def get_sentence(self) -> Dict:
        '''
        Return one result.

        Returns
        -------
        Result dictionary. Contains
            int: The search result number
            int: Total number of search results
            str: The sentence, in the `from_language`
            str: The translations of the sentence, in the `to_language`
            str: URL to source Tatoeba page
        '''
        if self.n > self.num_results:
            return {}
        if len(self.results) == 0:
            self.scrape()
        sentence, translations, url = self.results.pop(0)
        self.n += 1

        result = {
            'id': self.n,
            'total': self.num_results,
            'sentence': sentence,
            'translations': translations,
            'url': url
        }
        return result

    def scrape(self) -> None:
        '''
        Formulate the search query, and scrape the Tatoeba page for the example
        sentence search results.
        '''
        # Get some page soup!
        if self.page == 1:
            url = "https://tatoeba.org/en/sentences/search?from=%s&query=%s&to=%s" % (self.from_lang_code, self.query, self.to_lang_code)
        elif self.page <= self.num_pages:
            url = "https://tatoeba.org/en/sentences/search?from=%s&query=%s&to=%s&page=%i" % (self.from_lang_code, self.query, self.to_lang_code, self.page)

        page = requests.get(url)
        soup = BeautifulSoup(page.content, "html.parser")
        self.page += 1

        # First time called, find number of pages
        if self.num_pages == -1:
            matches = soup.find_all("div", class_="md-toolbar-tools")
            for match in matches:
                match_text = normalize(match.text)
                if 'result' in match_text:
                    self.num_results = int(re.findall("\((.*) result", match_text)[0])
                    self.num_pages = math.ceil(self.num_results / 10)
                    break

        # Find all search results
        # Extract sentence and translations
        page_matches = soup.find_all("div", class_="sentence-and-translations")
        for match in page_matches:
            match = match['ng-init']
            match2 = match[:match.find('highlightedText')] # Truncate
            match3 = re.findall("\"id\":(.*?),\"text\":\"(.*?)\",\"lang\":\"(.*?)\"", match2)

            sentence = [normalize(m[1]) for m in match3 if m[2] == self.from_lang_code][0]
            sentence_id = int([m[0] for m in match3 if m[2] == self.from_lang_code][0])
            link = "https://tatoeba.org/en/sentences/show/%i" % sentence_id
            translations = [normalize(m[1]) for m in match3 if m[2] == self.to_lang_code]
            translations = '\n'.join(translations)
            self.results.append((sentence, translations, link))

# x = TatoebaScraper("dog", "🇬🇧 English", "🇻🇳 Vietnamese")
# print(x.num_results)
# print(x.get_sentence())
# print(x.get_sentence())
# print(x.get_sentence())