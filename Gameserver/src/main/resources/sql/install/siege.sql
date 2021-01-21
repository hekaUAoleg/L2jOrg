DROP TABLE IF EXISTS `siege_mercenaries`;
DROP TABLE IF EXISTS siege_participants;
CREATE TABLE IF NOT EXISTS siege_participants
(
    `castle_id`         INT     NOT NULL DEFAULT 0,
    `clan_id`           INT     NOT NULL DEFAULT 0,
    `status`            ENUM ('ATTACKER', 'OWNER', 'WAITING', 'APPROVED', 'DECLINED'),
    `register_time`     DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    `recruit_mercenary` BOOLEAN NOT NULL DEFAULT FALSE,
    `mercenary_reward`  INT     NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `castle_id`),
    FOREIGN KEY (castle_id) REFERENCES castle (id) ON DELETE CASCADE,
    FOREIGN KEY (clan_id) REFERENCES clan_data (clan_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `siege_mercenaries`
(
    `castle_id` INT          NOT NULL DEFAULT 0,
    `clan_id`   INT          NOT NULL DEFAULT 0,
    `mercenary` INT UNSIGNED NOT NULL,

    PRIMARY KEY (`castle_id`, `clan_id`, `mercenary`),
    FOREIGN KEY (`castle_id`, `clan_id`) REFERENCES siege_participants (`castle_id`, `clan_id`),
    FOREIGN KEY (`mercenary`) REFERENCES characters (charId) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

