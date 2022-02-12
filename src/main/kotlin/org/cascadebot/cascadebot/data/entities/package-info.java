@TypeDefs({
        @TypeDef(name = "psql-enum", typeClass = EnumDBType.class),
        @TypeDef(name = "list-array", typeClass = ListArrayType.class),
        @TypeDef(name = "json", typeClass = JsonType.class)
})
package org.cascadebot.cascadebot.data.entities;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.cascadebot.cascadebot.data.EnumDBType;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;