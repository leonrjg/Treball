from playhouse.shortcuts import model_to_dict
from models import *
import falcon

class CategoryItems(object):
    def on_get(self, req, resp):
        q = (Category.select(Category.name, Category.id, fn.Count(Job.id).alias('count'))
            .join(Job).group_by(Category.id).order_by(SQL('count').desc()))
        results = [model_to_dict(x, fields_from_query=q) for x in q]

        resp.media = results

class CityItems(object):
    def on_get(self, req, resp):
        q = (Job.select(Job.city, fn.Count(Job.id).alias('count'))
        .group_by(Job.city)).order_by(SQL('count').desc())
        results = [model_to_dict(x, fields_from_query=q) for x in q]

        resp.media = results

class JobItems(object):
    def on_get(self, req, resp, id=None, category_id=None, city=None, search=None):
        if id is None:
            q = Job.select().order_by(Job.id.desc())
            if category_id is not None:
                q = q.where(Job.category_id == category_id)
            if city is not None:
                q = q.where(Job.city == city)
            if search is not None:
                q = q.where(Job.content.contains(search))
            results = [model_to_dict(x, recurse=False) for x in q.limit(100)]
        else:
            results = model_to_dict(Job.get_by_id(id), recurse=False)

        resp.media = results

app = falcon.API()

app.add_route('/jobs', JobItems())
app.add_route('/jobs/{id}', JobItems())

app.add_route('/cities', CityItems())
app.add_route('/jobs/city/{city}', JobItems())

app.add_route('/categories', CategoryItems())
app.add_route('/jobs/category/{category_id}', JobItems())
app.add_route('/jobs/category/{category_id}/city/{city}', JobItems())

app.add_route('/jobs/search/{search}', JobItems())
app.add_route('/jobs/search/{search}/city/{city}', JobItems())
app.add_route('/jobs/search/{search}/category/{category_id}', JobItems())
app.add_route('/jobs/search/{search}/city/{city}/category/{category_id}', JobItems())
