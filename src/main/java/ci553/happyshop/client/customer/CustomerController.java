package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;

public class CustomerController {
    public CustomerModel cusModel;

    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Search":
                cusModel.search();
                break;
            case "More Info":
                cusModel.checkInfo();
                break;
            case "My History":
                showHistory();
                break;
            case "Add to Wish List":
                cusModel.addToWishList();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Cancel Trolley":
                cusModel.trolleyCancel();
                break;
            case "Cancel Wish List":
                cusModel.wishListCancel();
                break;
            case "Check Out":
                cusModel.checkOut();
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
        }
    }

    public void showHistory() {

    }

}
