-- CREATE EXTENSION zhparser;
-- CREATE TEXT SEARCH CONFIGURATION chinese_zh (PARSER = zhparser);
-- ALTER TEXT SEARCH CONFIGURATION chinese_zh ADD MAPPING FOR n,v,a,i,e,l WITH simple;
CREATE INDEX idx_gin_poem_entity_content ON poem_entity USING gin (to_tsvector('chinese_zh', content));

CREATE INDEX idx_gin_poem_entity_title ON poem_entity USING gin (to_tsvector('chinese_zh', title));

INSERT INTO role_entity values (10001, 'ROLE_ADMIN');
INSERT INTO role_entity values (10002, 'ROLE_USER');