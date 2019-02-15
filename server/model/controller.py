# -*- coding: utf-8 -*-
from .common import *

class ModelWrapper(object):
    def __init__(self, clazz, connection, *args, **kwargs):
        self._wrapped = clazz(*args, **kwargs)
        self._connection = connection

    def __getattr__(self, attr):
        orig_attr = self._wrapped.__getattribute__(attr)
        if callable(orig_attr):
            def hooked(*args, **kwargs):
                self.setup()
                try:
                    result = orig_attr(*args, **kwargs)
                    if (result == self._wrapped):
                        return self
                    self.success()
                    return result
                except:
                    self.error()
                    raise
            return hooked
        else:
            return orig_attr

    def setup(self):
        # such as begin a transaction...
        pass

    def success(self):
        # such as commit a transaction... (must not fail!)
        pass

    def error(self):
        # such as rollback a transaction (must not fail!)
        pass
                

class ModelController(ModelObserver):
    def __init__(self, dbname):
        self._dbname = dbname

    def wrap(self, model):
        return ModelWrapper(model)
   
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

