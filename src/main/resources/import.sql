CREATE INDEX idx_gin_poem_entity_content ON poem_entity USING gin (to_tsvector('chinese_zh', content));

CREATE INDEX idx_gin_poem_entity_title ON poem_entity USING gin (to_tsvector('chinese_zh', title));

