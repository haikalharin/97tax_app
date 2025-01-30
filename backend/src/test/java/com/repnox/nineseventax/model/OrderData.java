package com.repnox.nineseventax.model;

import com.repnox.nineseventax.features.admin.AdminController;
import lombok.Data;

/**
 * Temporal POJO to workaround returned structure from the {@link com.repnox.nineseventax.features.admin.repo.OrderRepo#getOrdersList(AdminController.OrderSearchPaginationRequest)}.
 * TODO: Need to be OOP and to avoid usage of {@code Map<String, Object>} structures in API returning statements - so either use this POJO in the method above (moving from test to main) or even better to utilize already existing POJOs.
 */
@Data
public class OrderData {

    private String id;
    private String email;
    private String fname;
    private String lname;
    private String phone;
    private String product;
    private int is_california;
    private int is_georgia;
    private int is_illinois;
    private int is_michigan;
    private int is_new_jersey;
    private String order_type;
    private String status;
    private String created_date;

}
