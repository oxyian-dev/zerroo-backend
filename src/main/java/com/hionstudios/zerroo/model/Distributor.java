package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;

public class Distributor extends Model {
    public Distributor() {
    }

    public Distributor(long id, long parent, int placement, long referer) {
        set("id", id);
        set("parent_id", parent);
        set("placement", placement);
        set("referer_id", referer);
        
    }
}
