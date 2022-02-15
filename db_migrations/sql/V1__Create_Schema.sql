create type filter_type as enum ('WHITELIST', 'BLACKLIST');

create type filter_operator as enum ('AND', 'OR');

create type greeting_type as enum ('WELCOME', 'WELCOME_DM', 'GOODBYE');

create type filter_target_type as enum ('CHANNEL', 'ROLE', 'USER');

create type scheduled_action_type as enum ('REMINDER', 'UNMUTE', 'UNBAN', 'UNSLOWMODE');

create table guild
(
    guild_id   bigint not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    removed_at timestamp,
    constraint guild_pk
        primary key (guild_id)
);

create table guild_settings_core
(
    guild_id              bigint not null,
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
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_module
(
    guild_id      bigint  not null,
    core          boolean not null,
    useful        boolean not null,
    moderation    boolean not null,
    management    boolean not null,
    informational boolean not null,
    constraint guild_module_pk
        primary key (guild_id),
    constraint guild_module_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_todolist
(
    name         varchar(255) not null,
    guild_id     bigint       not null,
    message_id   bigint,
    channel_id   bigint,
    current_item integer default 0,
    constraint guild_todolist_pk
        primary key (name, guild_id),
    constraint guild_todolist_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_todolist_item
(
    id            uuid default gen_random_uuid(),
    todolist_name varchar(255),
    guild_id      bigint,
    text          varchar(255) not null,
    done          boolean      not null,
    constraint guild_todolist_item_pk
        primary key (id),
    constraint guild_todolist_item_fk
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
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_modlog
(
    id            uuid default gen_random_uuid(),
    channel_id    bigint not null,
    guild_id      bigint not null,
    webhook_id    bigint,
    webhook_token varchar(255),
    constraint guild_modlog_pk
        primary key (id, channel_id, guild_id),
    constraint guild_modlog_fk
        foreign key (guild_id) references guild
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
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_tag
(
    guild_id bigint       not null,
    name     varchar(255) not null,
    content  text         not null,
    category varchar(255),
    constraint guild_tag_pk
        primary key (guild_id, name),
    constraint guild_tag_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_filter
(
    name     varchar(255)    not null,
    guild_id bigint          not null,
    enabled  boolean default true,
    type     filter_type     not null,
    operator filter_operator not null,
    commands varchar(255)[]  not null,
    constraint guild_command_filter_pk
        primary key (name, guild_id),
    constraint guild_command_filter_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_permission_group
(
    guild_id    bigint       not null,
    name        varchar(255) not null,
    position    int          not null,
    permissions varchar(255)[] default array[]::varchar(255)[],
    roles       bigint[]       default array[]::bigint[],
    constraint guild_permission_group_pk
        primary key (name, guild_id),
    constraint guild_permission_group_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_permission_user
(
    user_id     bigint not null,
    guild_id    bigint not null,
    permissions varchar(255)[] default array[]::varchar(255)[],
    constraint guild_permission_user_pk
        primary key (user_id, guild_id),
    constraint guild_permission_user_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_permission_user_membership
(
    group_id varchar(10) not null,
    user_id  bigint      not null,
    guild_id bigint      not null,
    constraint guild_permission_user_membership_pk
        primary key (group_id, user_id, guild_id),
    constraint guild_permission_user_membership_user_fk
        foreign key (user_id, guild_id) references guild_permission_user
            on delete cascade,
    constraint guild_permission_user_membership_group_fk
        foreign key (group_id, guild_id) references guild_permission_group
            on delete cascade
);

create table guild_autorole
(
    id       uuid default gen_random_uuid(),
    guild_id bigint not null,
    role_id  bigint not null,
    constraint guild_autorole_pk
        primary key (id),
    constraint guild_autorole_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create unique index guild_autorole_role_id_uindex
    on guild_autorole (role_id);

create table guild_greeting
(
    id       uuid default gen_random_uuid(),
    guild_id bigint,
    type     greeting_type,
    content  text,
    weight   integer,
    constraint guild_greeting_pk
        primary key (id),
    constraint guild_greeting_fk
        foreign key (guild_id) references guild
            on delete cascade
);

create table guild_filter_criteria
(
    id          uuid default gen_random_uuid(),
    filter_name varchar(255)         not null,
    guild_id    bigint               not null,
    type        filter_target_type   not null,
    target_id   bigint               not null,
    constraint guild_filter_criteria_pk
        primary key (id),
    constraint guild_filter_criteria_fk
        foreign key (filter_name, guild_id) references guild_filter
            on delete cascade
);

create table guild_modlog_event_enabled
(
    id         uuid default gen_random_uuid(),
    modlog_id  uuid         not null,
    channel_id bigint       not null,
    guild_id   bigint       not null,
    event      varchar(255) not null,
    constraint guild_modlog_event_enabled_pk
        primary key (id),
    constraint guild_modlog_event_enabled_fk
        foreign key (modlog_id, channel_id, guild_id) references guild_modlog
            on delete cascade
);

create table scheduled_action_data
(
    id bigserial,
    target_id bigint,
    reminder text,
    is_dm bool,
    old_slowmode int,
    constraint scheduled_action_data_pk
        primary key (id)
);

create table scheduled_action
(
    id bigserial,
    type scheduled_action_type not null,
    data_id int not null,
    guild_id bigint not null,
    channel_id bigint not null,
    user_id bigint not null,
    creation_time timestamp not null,
    execution_time timestamp not null,
    constraint scheduled_action_pk
        primary key (id),
    constraint scheduled_action_data_fk
        foreign key (data_id) references scheduled_action_data
            on delete cascade
);