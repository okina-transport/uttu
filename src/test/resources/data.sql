INSERT INTO codespace
(pk,
 version,
 xmlns,
 xmlns_url,
 created,
 created_by,
 changed,
 changed_by)
VALUES (-1,
        0,
        'TST',
        'http://www.rutebanken.org/ns/tst',
        Now(),
        'test',
        Now(),
        'test');

INSERT INTO provider
(pk,
 version,
 code,
 NAME,
 codespace_pk,
 created,
 created_by,
 changed,
 changed_by)
VALUES (-1,
        0,
        'tst',
        'Test provider',
        (SELECT pk
         FROM codespace
         WHERE xmlns = 'TST'),
        Now(),
        'test',
        Now(),
        'test');

INSERT INTO codespace
(pk,
 version,
 xmlns,
 xmlns_url,
 created,
 created_by,
 changed,
 changed_by)
VALUES (0,
        0,
        'FOO',
        'http://www.rutebanken.org/ns/foo',
        Now(),
        'test',
        Now(),
        'test');

INSERT INTO provider
(pk,
 version,
 code,
 NAME,
 codespace_pk,
 created,
 created_by,
 changed,
 changed_by)
VALUES (0,
        0,
        'foo',
        'Foo provider',
        0,
        Now(),
        'test',
        Now(),
        'test');

INSERT INTO flexible_stop_place
(pk,
 changed,
 changed_by,
 created,
 created_by,
 version,
 netex_id,
 transport_mode,
 provider_pk)
VALUES (-1,
        Now(),
        'test',
        Now(),
        'test',
        0,
        'TST:FlexibleStopPlace:1',
        'BUS',
        (SELECT pk
         FROM codespace
         WHERE xmlns = 'TST'));

INSERT INTO network
(pk,
 changed,
 changed_by,
 created,
 created_by,
 "version",
 netex_id,
 description,
 "name",
 private_code,
 short_name,
 authority_ref,
 provider_pk,
 original_id,
 dataset_id)
VALUES (-1,
        Now(),
        'test',
        Now(),
        'test',
        0,
        'TST:Network:1',
        '',
        'TEST',
        '',
        '',
        'TST',
        (SELECT pk
         FROM provider
         WHERE code = 'tst'),
        '',
        '');