# -*- coding: utf-8 -*-
"""
   Copyright 2019 Jari ojala (jari.ojala@iki.fi)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
"""
import re

def jsonify(entityException):
    return { "message": str(entityException) }

def accept_filter(entity):
    """A concrete filter accepting every entity given (returns always True).
        See e.g. create_equality_filter() to get grasp of filters."""
    return True

def create_equality_filter(field_name, accepted_value, next_filter):
    """Returns a filter, that accepts the given entity (ie. returns True)
    if the given entity have field 'field_name' with value equal to
    'accepted_value'. If not accepted, False is returned. In case of
    accept, potential changed filter 'next_filter' will be invoked."""
    def f(entity):
        if accepted_value == entity[field_name]:
            if next_filter:
                return next_filter(entity)
            return True
        return False
    return f

def create_in_filter(field_name, accepted_values, next_filter):
    """Returns a filter that accepts the given entity in case the entity
    contains the field 'field_name' with a value available in the iterable
    'accepted_values'. For more about filters, refer to
    create_equality_filter()."""
    def f(entity):
        if field_name in entity and entity[field_name] in accepted_values:
            if next_filter:
                return next_filter(entity)
            return True
        return False
    return f

def create_case_insensitive_equality_filter(field_name, accepted_string,
                                            next_filter):
    """Returns a filter that accepts the given entity in case the entity
    contains the field 'field_name' with the value case-insensitively
    equal to 'accepted_string'. For case sensitive equality, use
    create_equality_filter()."""
    def f(entity):
        if (accepted_string and field_name in entity
            and accepted_string.lower() == entity[field_name].lower()):
            if next_filter:
                return next_filter(entity)
            return True
        return False
    return f

def create_case_insensitive_substring_filter(field_name, accepted_substring,
                                             next_filter):
    """Returns a filter that accepts the given entity in case the entity
    contains the field 'field_name' with a value contained by
    'accepted_substring' when comparing in case-insensitive manner. For
    case sensitive filter, use create_in_filter()."""
    def f(entity):
        if (accepted_substring and field_name in entity
            and accepted_substring.lower() in entity[field_name].lower()):
            if next_filter:
                return next_filter(entity)
            return True
        return False
    return f

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
