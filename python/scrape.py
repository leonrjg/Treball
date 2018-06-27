from time import sleep
from urlparse import urljoin
from bs4 import BeautifulSoup
import requests

class Scrape:
    def __init__(self, url):
        self.url = url
        self.headers = {"User-Agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3351.181 Safari/537.36"}
        self.html = self.get_html(url)

    def get_html(self, url, attempts=3):
        while attempts != 0:
            try:
               return requests.get(url, headers=self.headers, timeout=5).content
            except requests.exceptions.RequestException:
               attempts = attempts - 1
               sleep(1)

    def get_selector(self, selector, all=False, text=True):
        soup = BeautifulSoup(self.html, "html.parser")
        if all is False:
            r = [soup.select_one(selector)]
        else:
            r = soup.select(selector)
        if None in r:
            return ""
        values = [urljoin(self.url, x["href"]) if "href" in selector else x["src"] if "img" in selector else x.get_text(separator="\n").strip() for x in r]
        if all is False:
            values = values[0]
        return values
