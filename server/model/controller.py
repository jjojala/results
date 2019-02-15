# -*- coding: utf-8 -*-
from .common import *
import sqlite3

class ModelWrapper(object):
    def __init__(self, controller, wrappee):
        self._controller = controller
        self._wrapped = wrappee

    def __getattr__(self, member_name):
        member = self._wrapped.__getattribute__(member_name)
        if callable(member):
            def decorator(*args, **kwargs):
                self.setup()
                try:
                    result = member(*args, **kwargs)
                    if (result == self._wrapped):
                        return self
                    self.success()
                    return result
                except:
                    self.error()
                    raise
            return decorator
        else:
            return member

    def setup(self):
        # such as begin a transaction...
        pass

    def success(self):
        self._controller.get_connection().commit()

    def error(self):
        self._controller.get_connection().rollback()

class ModelController:
    def __init__(self, dbname):
        self._connection = sqlite3.connect(dbname)

    def get_connection(self):
        return self._connection

    def wrap(self, model):
        return ModelWrapper(self, model)
   
    def created(self, itemType, id, item):
        print("Created {} with id {}: {}".format(itemType, id, item))
    
    def updated(self, itemType, id, old, new):
        print("Updated {} with id {}:\n\told: {}\n\tnew: {}".format(
            itemType, id, old, new))
    
    def removed(self, itemType, id, removedItem):
        print("Removed {} with id {}: removed item: {}".format(
            itemType, id, removedItem))
    
    def patched(self, itemType, id, diff, old, new):
        print("Patched {} with id {}:\n\tdiff: {}\n\told: {}\n\tnew: {}".format(
            itemType, id, diff, old, new))

