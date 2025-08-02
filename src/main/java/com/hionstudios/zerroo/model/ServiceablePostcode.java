package com.hionstudios.zerroo.model;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;

@CompositePK({ "postcode", "courier_id" })
public class ServiceablePostcode extends Model {
}
