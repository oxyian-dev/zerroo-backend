package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({"list_id", "specification_id"})
public class SpecificationGroup extends Model {
}
