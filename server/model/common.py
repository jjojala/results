# -*- coding: utf-8 -*-


def jsonify(entityException):
    return { "message": str(entityException) }

class EntityException(Exception):
    pass

class IllegalEntity(EntityException):
    def str(entityType, id, msg):
        return "Entity {} with id {} is illegal: {}".format(
            entityType, id, msg)

    def __init__(self, entityType, id, msg):
        self._entityType = entityType
        self._id = id
        self._msg = msg

    def __str__(self):
        return IllegalEntity.str(self._entityType,
                                 self._id, self._msg)

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
        return EntityConstraintViolated.str(
            self._entityType, self._id, self._msg)
