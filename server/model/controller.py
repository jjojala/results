# -*- coding: utf-8 -*-
from .common import *

class ModelController(ModelObserver):
    def created(self, itemType, id, item):
        pass

    def updated(self, itemType, id, item):
        pass

    def removed(self, itemType, id):
        pass

    def patched(self, itemType, id, diff, item):
        pass

