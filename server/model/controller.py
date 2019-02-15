# -*- coding: utf-8 -*-
from .common import *

class ModelController(ModelObserver):
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

