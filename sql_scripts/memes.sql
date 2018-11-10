DROP TABLE  IF EXISTS RawPosts, AnalizedPosts, Timestamps CASCADE;


CREATE TABLE RawPosts (
	groupId BIGINT NOT NULL,
	postId BIGINT NOT NULL,
	timestamp BIGINT NOT NULL,
	text TEXT NOT NULL,
	photoPaths TEXT NOT NULL,
	sended BOOLEAN NOT NULL,
	PRIMARY KEY (groupId, postId)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE INDEX timestamp ON RawPosts(timestamp);

CREATE TABLE AnalizedPosts (
	groupId BIGINT NOT NULL,
	postId BIGINT NOT NULL,
	timestamp BIGINT NOT NULL,
	text TEXT NOT NULL,
	PRIMARY KEY (groupId, postId)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE INDEX timestamp ON AnalizedPosts(timestamp);
CREATE INDEX groupId ON AnalizedPosts(groupId);
CREATE FULLTEXT INDEX text ON AnalizedPosts(text);

CREATE TABLE Timestamps (
	minTimestamp BIGINT NOT NULL,
	maxTimestamp BIGINT NOT NULL
);