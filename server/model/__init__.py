# -*- coding: utf-8 -*-

class EntityException(Exception):
    pass

class EntityAlreadyExists(EntityException):
    def str(entityType, id):
        return "Entity {} with id {} already exists!".format(
            entityType, id)

    def __init__(self, entityType, id):
        self._entityType = entityType
        self._id = id

    def __str__(self):
        return EntityAlreadyExists.str(self._entityType, self._id)

class EntityNotFound(EntityException):
    def str(entityType, id):
        return "Entity {} with id {} not found!".format(
            entityType, id)

    def __init__(self, entityType, id):
        self._entityType = entityType
        self._id = id

    def __str__(self):
        return EntityNotFound.str(self._entityType, self._id)

class EntityConstraintViolated(EntityException):
    def str(entityType, id, msg):
        return "Constraint violated for {} with id {}: {}".format(
            entityType, id, msg)

    def __init__(self, entityType, id, msg):
        self._entityType = entityType
        self._id = id
        self._msg = msg

    def __str__(self):
        return EntityConstraintViolation.str(
            self._entityType, self._id, self._msg)

class EventModel:
    def __init__(self):
        self._items = []

    def list(self):
        return self._items

    def get(self, id):
        for i in self._items:
            if (id == i["id"]):
                return i
        return None
 
    def create(self, item):
        for i in self._items:
            if (item["id"] == i["id"]):
                raise EntityAlreadyExists("Event", item["id"])
        self._items.append(item)
        return item

    def update(self, item):
        for i in range(len(self._items)):
            if (item["id"] == self._items[i]["id"]):
                self._items[i] = item
                return item
        raise EntityNotFound("Event", item["id"])

    def remove(self, id):
        for i in range(len(self._items)):
            if (id == self._items[i]["id"]):
                del self._items[i]
                return True
        raise EntityNotFound("Event", id)

    def patch(self, id, patcher):
        try:
            for i in range(len(self._items)):
                if (id == self._items[i]["id"]):
                    self._items[i] = patcher(self._items[i])
                    return self._items[i]
            raise EntityNotFound("Event", id)
        except PatchConflict as ex:
            raise EntityConstraintViolated("Event", id, str(ex))

class TagModel:
    def __init__(self):
        self._items = []

    def list(self):
        return self._items

    def get(self, id):
        for i in self._items:
            if (id == i["id"]):
                return i
        return None
 
    def create(self, item):
        for i in self._items:
            if (item["id"] == i["id"]):
                raise EntityAlreadyExists("Tag", item["id"])
        self._items.append(item)
        return item

    def update(self, item):
        for i in range(len(self._items)):
            if (item["id"] == self._items[i]["id"]):
                self._items[i] = item
                return item
        raise EntityNotFound("Tag", item["id"])

    def remove(self, id):
        for i in range(len(self._items)):
            if (id == self._items[i]["id"]):
                del self._items[i]
                return True
        raise EntityNotFound("Tag", id)

    def patch(self, id, patcher):
        try:
            for i in range(len(self._items)):
                if (id == self._items[i]["id"]):
                    self._items[i] = patcher(self._items[i])
                    return self._items[i]
            raise EntityNotFound("Tag", id)
        except PatchConflict as ex:
            raise EntityConstraintViolated("Tag", id, str(ex))
