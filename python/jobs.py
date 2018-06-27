import time
from scrape import Scrape
from models import *

class Jobs:
    def __init__(self):
        db.connect()
        db.create_tables(TABLES, safe=True)

    def populate(self):
        scrape_details = Scrape_Info.select(Scrape_Info, Site).join(Site)
        columns = Job._meta.sorted_field_names[1:]
        for details in scrape_details:
            scrape = Scrape(details.url)
            site = details.site_id
            links = scrape.get_selector(site.link_selector, all=True)
            selectors = [site.date_selector, site.city_selector, site.title_selector, site.location_selector, site.company_selector, site.content_selector, site.image_selector]
            foreign_keys = [details.site_id.id, details.category_id.id]
            data = []
            updated = False
            for link in links:
                if Job.select().where(Job.link == link).exists():
                    updated = True
                    break
                scrape_link = Scrape(link)
                offer = [link]
                for selector in selectors:
                    offer.append(scrape_link.get_selector(selector))
                offer = foreign_keys + offer
                data.append(dict(zip(columns, offer)))
                time.sleep(0.5)
            if updated is False:
                with db.atomic():
                    Job.insert_many(list(reversed(data))).execute()

if __name__ == "__main__":
    Jobs().populate()
