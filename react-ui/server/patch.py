import copy

class PatchConflict(Exception):
    def __init__(self, key, expected, actual):
        self._msg = "Conflict in attribute \'{}\': expecting \'{}\' but got \'{}\'".format(key, expected, actual)

    def __str__(self):
        return self._msg

def diff(a, b):
    """Get a diff -object of 'a' to 'b'

        Given, the a and b..
        a = { "id": 1, name:"My name", args: [ 1, 2 ] }
        b = { "id": 2, name:"Your name", args: [ 2, 3 ] }

        .. the diff is:
        diff(a, b) = {
          "name": [ "My Name", "Your name" ],
          "args": [ [ 1, 2 ], [ 2, 3 ] ]
        }

        If the two objects have no difference, then an empty
        object "{}" will be returned.
    """
    def diff_value(value_a, value_b):
        if (value_a == value_b):
            return None
        if (value_a == None or value_b == None):
            return [value_a, value_b]
        if (isinstance(value_a, dict)):
            return diff_dict(value_a, value_b)
        return [value_a, value_b]
        
    def diff_dict(dict_a, dict_b):
        d = {}
        for key,val in dict_a.items():
            c = diff(val, dict_b[key])
            if (c != None):
                d[key] = c
        return d

    if (isinstance(a, dict)):
        return diff_dict(a, b)

    return diff_value(a, b)

def patch(target, diff, key=None):
    """Patch object target with diff-object and return a new, patched object.

    The 'diff' must apply the rules of an diff-object briefly described
    with the diff() function.
    """
    def patch_value(k, t, c):
        if (t == c[0]):
            return c[1]
        raise PatchConflict(k, c[0], t)

    def patch_dict(k, t, d):
        r = {} # result
        for k,v in t.items(): # for 'key','value:' in 'target'
            if (k in d.keys()):  # if 'diff' contains 'key'
                r[k] = patch(v, d[k], ks)
            else:
                r[k] = copy.deepcopy(t[k]) # no 'change' so copy as such

        return r

    if (isinstance(diff, dict)):
        return patch_dict(key, target, diff)
    return patch_value(key, target, diff)
