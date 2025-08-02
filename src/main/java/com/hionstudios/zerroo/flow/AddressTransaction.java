package com.hionstudios.zerroo.flow;

import static com.hionstudios.StringUtil.nullify;

import com.hionstudios.MapResponse;
import com.hionstudios.db.Handler;
import com.hionstudios.iam.UserUtil;
import com.hionstudios.zerroo.model.Address;
import com.hionstudios.time.TimeUtil;

public class AddressTransaction {
    public MapResponse addAddress(
            String name,
            String firstname,
            String lastname,
            String phone,
            String altPhone,
            String email,
            String address1,
            String address2,
            String postcode,
            String landmark,
            String city,
            String state,
            boolean isDefault) {
        Address address = new Address();
        altPhone = nullify(altPhone);
        Address.construct(address,
                name,
                firstname,
                lastname,
                phone,
                altPhone,
                email,
                address1,
                address2,
                postcode,
                landmark,
                city,
                state,
                isDefault);
        long userid = UserUtil.getUserid();
        address.set("distributor_id", userid);
        address.set("time", TimeUtil.currentTime());
        if (isDefault) {
            Address.update("is_default = ?", "distributor_id = ?", false, userid);
        }
        return address.insert() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse editAddress(
            long id,
            String name,
            String firstname,
            String lastname,
            String phone,
            String altPhone,
            String email,
            String address1,
            String address2,
            String postcode,
            String landmark,
            String city,
            String state,
            boolean isDefault) {
        long user = UserUtil.getUserid();
        altPhone = nullify(altPhone);
        Address address = Address.findFirst("id = ? And distributor_id = ?", id, user);
        Address.construct(
                address,
                name,
                firstname,
                lastname,
                phone,
                altPhone,
                email,
                address1,
                address2,
                postcode,
                landmark,
                city,
                state,
                isDefault);
        if (isDefault) {
            Address.update("is_default = ?", "distributor_id = ? And id != ?",
                    false, user, id);
        }
        return address.saveIt() ? MapResponse.success() : MapResponse.failure();
    }

    public MapResponse makeDefault(long id) {
        long user = UserUtil.getUserid();
        Address.update("is_default = ?", "distributor_id = ?", false, user);
        return Address.update("is_default = ?",
                "id = ? And distributor_id = ?",
                true, id, user) == 1
                        ? MapResponse.success()
                        : MapResponse.failure();
    }

    public MapResponse delete(long id) {
        long user = UserUtil.getUserid();
        return Address.delete("id = ? And distributor_id = ?", id, user) == 1
                ? MapResponse.success()
                : MapResponse.failure();
    }

    public MapResponse view() {
        long userId = UserUtil.getUserid();
        String sql = "Select * From Addresses Where distributor_id = ? Order By Is_Default Desc";
        MapResponse response = new MapResponse(1);
        response.put("addresses", Handler.findAll(sql, userId));
        return response;
    }

    public MapResponse view(long id) {
        long userid = UserUtil.getUserid();
        String sql = "Select * From Addresses Where distributor_id = ? And Id = ?";
        return Handler.findFirst(sql, userid, id);
    }
}
