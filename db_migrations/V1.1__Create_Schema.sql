create type filter_type as enum ('WHITELIST', 'BLACKLIST');

create type filter_operator as enum ('AND', 'OR');

create type greeting_type as enum ('WELCOME', 'WELCOME_DM', 'GOODBYE');

create type filter_criteria_type as enum ('CHANNEL', 'ROLE', 'USER');

create table guilds
(
    guild_id   bigint not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    removed_at timestamp,
    constraint guilds_pk
        primary key (guild_id)
);

create table guild_settings_core
(
    guild_id              bigint not null,
    mention_prefix        boolean default false,
    delete_command        boolean default true,
    use_embeds            boolean default true,
    perm_errors           boolean default true,
    module_errors         boolean default true,
    admins_all_perms      boolean default true,
    help_hide_no_perms    boolean default true,
    help_show_all_modules boolean default false,
    constraint guild_core_settings_pk
        primary key (guild_id),
    constraint guild_settings_core_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_modules
(
    guild_id      bigint  not null,
    core          boolean not null,
    useful        boolean not null,
    moderation    boolean not null,
    management    boolean not null,
    music         boolean not null,
    informational boolean not null,
    fun           boolean not null,
    constraint guild_modules_pk
        primary key (guild_id),
    constraint guild_modules_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_todolist
(
    name         varchar(255) not null,
    guild_id     bigint       not null,
    message_id   bigint,
    channel_id   bigint,
    current_item integer,
    constraint guild_todolist_pk
        primary key (name, guild_id),
    constraint guild_todolist_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_todolist_items
(
    id            serial,
    todolist_name varchar(255),
    guild_id      bigint,
    text          varchar(255) not null,
    done          boolean      not null,
    constraint guild_todolist_items_pk
        primary key (id),
    constraint guild_todolist_items_fk
        foreign key (guild_id, todolist_name) references guild_todolist (guild_id, name)
            on delete cascade
);

create table guild_settings_moderation
(
    guild_id          bigint not null,
    purge_pinned      boolean      default false,
    respect_hierarchy boolean      default true,
    mute_role_name    varchar(255) default 'Muted'::character varying,
    mute_role_id      bigint,
    constraint guild_settings_moderation_pk
        primary key (guild_id),
    constraint guild_settings_moderation_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_modlog_events
(
    id            varchar(10) not null,
    channel_id    bigint      not null,
    guild_id      bigint      not null,
    webhook_id    bigint,
    webhook_token varchar(255),
    constraint guild_modlog_events_pk
        primary key (id, channel_id, guild_id),
    constraint guild_modlog_events_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_settings_management
(
    guild_id             bigint not null,
    display_filter_error boolean default false,
    warn_over_10         boolean default true,
    constraint guild_settings_management_pk
        primary key (guild_id),
    constraint guild_settings_management_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_tags
(
    guild_id bigint       not null,
    name     varchar(255) not null,
    content  text         not null,
    category varchar(255) not null,
    constraint guild_tags_pk
        primary key (guild_id, name),
    constraint guild_tags_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_filters
(
    name     varchar(255)    not null,
    guild_id bigint          not null,
    enabled  boolean default true,
    type     filter_type     not null,
    operator filter_operator not null,
    commands varchar(255)[]  not null,
    constraint guild_command_filters_pk
        primary key (name, guild_id),
    constraint guild_command_filters_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_perms_groups
(
    group_id varchar(10)  not null,
    guild_id bigint       not null,
    name     varchar(255) not null,
    constraint guild_permissions_groups_pk
        primary key (group_id, guild_id),
    constraint guild_permissions_groups_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_perms_users
(
    user_id  bigint not null,
    guild_id bigint not null,
    constraint guild_permissions_users_pk
        primary key (user_id, guild_id),
    constraint guild_permissions_users_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_perms_user_membership
(
    group_id varchar(10) not null,
    user_id  bigint      not null,
    guild_id bigint      not null,
    constraint guild_permissions_user_membership_pk
        primary key (group_id, user_id, guild_id),
    constraint guild_permissions_user_membership_user_fk
        foreign key (user_id, guild_id) references guild_perms_users
            on delete cascade,
    constraint guild_permissions_user_membership_group_fk
        foreign key (group_id, guild_id) references guild_perms_groups
            on delete cascade
);

create table guild_autoroles
(
    guild_id bigint,
    role_id  bigint,
    constraint guild_autoroles_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create unique index guild_autoroles_role_id_uindex
    on guild_autoroles (role_id);

create table guild_greetings
(
    id       serial,
    guild_id bigint,
    type     greeting_type,
    content  text,
    weight   integer,
    constraint guild_greetings_pk
        primary key (id),
    constraint guild_greetings_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_filters_criteria
(
    id          serial,
    filter_name varchar(255)         not null,
    guild_id    bigint               not null,
    type        filter_criteria_type not null,
    criteria_id bigint               not null,
    constraint guild_filters_criteria_pk
        primary key (id),
    constraint guild_filters_criteria_fk
        foreign key (filter_name, guild_id) references guild_filters
            on delete cascade
);

create table guild_modlog_events_enabled
(
    id         serial,
    modlog_id  varchar(10),
    channel_id bigint,
    guild_id   bigint,
    event      varchar(255) not null,
    constraint guild_modlog_events_enabled_pk
        primary key (id),
    constraint guild_modlog_events_enabled_fk
        foreign key (modlog_id, channel_id, guild_id) references guild_modlog_events
            on delete cascade
);

create table guild_perms_groups_entries
(
    id         serial,
    group_id   varchar(10)  not null,
    guild_id   bigint       not null,
    permission varchar(255) not null,
    constraint guild_perms_groups_entries_pk
        primary key (id),
    constraint guild_perms_groups_entries_fk
        foreign key (group_id, guild_id) references guild_perms_groups
            on delete cascade
);

create table guild_perms_users_entries
(
    id         serial,
    user_id    bigint       not null,
    guild_id   bigint       not null,
    permission varchar(255) not null,
    constraint guild_perms_users_entries_pk
        primary key (id),
    constraint guild_perms_users_entries_fk
        foreign key (user_id, guild_id) references guild_perms_users
            on delete cascade
);

create table guild_perms_groups_roles
(
    id       serial,
    group_id varchar(10) not null,
    guild_id bigint      not null,
    role_id  bigint      not null,
    constraint guild_perms_groups_roles_pk
        primary key (id),
    constraint guild_perms_groups_entries_fk
        foreign key (group_id, guild_id) references guild_perms_groups
            on delete cascade
);

create table guild_settings_music
(
    guild_id           bigint not null,
    preverse_volume    boolean  default true,
    preserve_equalizer boolean  default true,
    join_on_play       boolean  default true,
    volume             smallint default 100,
    constraint guilds_settings_music_pk
        primary key (guild_id),
    constraint guilds_settings_music_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

create table guild_equalizer_bands
(
    id       smallint not null,
    guild_id bigint   not null,
    value    real     not null,
    constraint guild_equalizer_bands_pk
        primary key (id, guild_id),
    constraint guild_equalizer_bands_fk
        foreign key (guild_id) references guilds
            on delete cascade
);

