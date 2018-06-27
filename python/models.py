from peewee import *
import datetime


db = SqliteDatabase("treball.db")

class BaseModel(Model):
    class Meta:
        database = db

class Site(BaseModel):
    id = AutoField()
    name = CharField()
    date_selector = CharField()
    city_selector = CharField()
    title_selector = CharField()
    location_selector = CharField()
    company_selector = CharField()
    content_selector = CharField()
    link_selector = CharField()
    image_selector = CharField()

class Category(BaseModel):
    id = AutoField()
    name = CharField(unique=True)

class Scrape_Info(BaseModel):
    id = AutoField()
    url = CharField(unique=True)
    site_id = ForeignKeyField(Site, backref="site")
    category_id = ForeignKeyField(Category, backref="category")

class Job(BaseModel):
    id = AutoField()
    site_id = ForeignKeyField(Site, backref='site')
    category_id = ForeignKeyField(Category, backref='category')
    link = CharField(unique=True)
    post_date = DateTimeField(default=datetime.datetime.now)
    city = CharField()
    title = CharField()
    location = CharField()
    company = CharField()
    content = TextField()
    image = CharField()

TABLES = [Site, Category, Scrape_Info, Job]
