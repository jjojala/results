/* 
 * Copyright (C) 2016 Jari Ojala (jari.ojala@iki.fi)
 */

describe('utils.Uuid', function() {

    var Uuid;
    
    beforeEach(module('utils'));
    
    beforeEach(inject(function(_Uuid_) {
        Uuid = _Uuid_;
    }));
    
    it('Should be defined', function() {
        expect(Uuid.randomUUID).toBeDefined();
    });
    
    it('Should have correct syntax', function() {
        expect(Uuid.randomUUID()).toMatch(/[0-9a-f\-]{36}/);
    });
    
    it('Should produce unique values', function() {
        var obj = {};
        for (i = 0; i < 100000; i++) {
            var v = Uuid.randomUUID();
            expect(obj[v]).toBeUndefined();
            obj[v] = null;
        }
    });
});

