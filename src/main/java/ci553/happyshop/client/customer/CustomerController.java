package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.ProductListFormatter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    public CustomerModel cusModel;
    public CustomerView cusView = new CustomerView();

    private CustomerUserManage cusUserManage = new CustomerUserManage();
    private CustomerUser currentUser;

    boolean existsAccount = true;

    public CustomerController(CustomerView cusView, CustomerModel cusModel) {
        this.cusView = cusView;
        this.cusModel = cusModel;
    }

    /// ----------------------------------------------------------------------------------------------------------------

    public void login(String accountNumber, String password) {
        currentUser = cusUserManage.authenticate(accountNumber, password);
        if (currentUser != null) {
            cusModel.setCurrentUser(currentUser);
            cusView.tfAccID.setText("");
            cusView.pfAccPwd.setText("");
            cusView.laLoginMsg.setText("");
            cusView.showSearchPage();
            System.out.println(currentUser.getAccountNumber() + " " + currentUser.getFirstName() + " " + currentUser.getLastName() + " Logged in");

            ArrayList<Product> currentUserWishList = currentUser.getWishList();
            StringBuilder wishListSB = new StringBuilder();
            for (Product product : currentUserWishList) {
                wishListSB.append(product.toString()).append("\n");
            }
            cusView.taWishList.setText(wishListSB.toString());

        } else {
            cusView.tfAccID.setText("");
            cusView.pfAccPwd.setText("");
            cusView.laLoginMsg.setText("Invalid login credentials!");
        }
    }

    /// ----------------------------------------------------------------------------------------------------------------

    public void handleCreateAccount(String accountNumber, String password, String firstName, String lastName, String confirmPassword, String email, LocalDate birthDate) {
        existsAccount = cusUserManage.accountExists(accountNumber);

        if ( firstName.equals("") ) {
            cusView.tfCreateAccUserFN.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER your FIRST NAME");
        } else if ( lastName.equals("") ) {
            cusView.tfCreateAccUserLN.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER your LAST NAME");
        } else if ( accountNumber.equals("") ) {
            cusView.tfCreateAccID.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER an ACCOUNT NUMBER of your choice");
        } else if ( password.equals("") ) {
            cusView.pfCreateAccPwd.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER a PASSWORD of your choice");
        }  else if ( confirmPassword.equals("") ) {
            cusView.pfCreateAccPwd2.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER the same PASSWORD");
        } else if ( email.equals("") ) {
            cusView.tfCreateAccEmail.setText("");
            cusView.taCreateAccMsg.setText("Please ENTER your EMAIL");
        } else if ( birthDate == null ) {
            cusView.dpCreateAccBDay.setValue(LocalDate.now());
            cusView.taCreateAccMsg.setText("Please SELECT your DATE OF BIRTH");
        } else {
            if ( !existsAccount ) {
                if ( password.equals(confirmPassword) ) {
                    String newAccountNumber = accountNumber;
                    String newPassword = password;
                    String newFirstName = firstName;
                    String newLastName = lastName;
                    String newEmail = email;
                    LocalDate newBirthDate = birthDate;

                    CustomerUser newUser = new CustomerUser(newAccountNumber, newPassword, newFirstName, newLastName, newEmail, newBirthDate);
                    boolean userAdded = cusUserManage.addUser(newUser);

                    if (userAdded) {
                        cusView.showLoginPage();

                        cusView.tfCreateAccID.setText("");
                        cusView.pfCreateAccPwd.setText("");
                        cusView.tfCreateAccUserFN.setText("");
                        cusView.tfCreateAccUserLN.setText("");
                        cusView.pfCreateAccPwd2.setText("");
                        cusView.tfCreateAccEmail.setText("");
                        cusView.dpCreateAccBDay.setValue(LocalDate.now());

                        System.out.println("Account Created\n" +
                                "Account Number : " + accountNumber + "\n" +
                                "Password : " + password + "\n" +
                                "First Name : " + firstName + " " + "Last Name : " + lastName + "\n" +
                                "Email : " + email + "\n" +
                                "Date Of Birth : " + birthDate);
                    } else {
                        cusView.taCreateAccMsg.setText("Error: Unable to create account");
                    }
                } else {
                    cusView.pfCreateAccPwd.setText("");
                    cusView.pfCreateAccPwd2.setText("");
                    cusView.taCreateAccMsg.setText("Password does not match");
                }
            } else {
                cusView.tfAccID.setText("");
                cusView.taCreateAccMsg.setText("Account Number already exists, please choose another account number");
            }
            //view.createAccMessageTextArea.setText("Please ENTER the information to create an account");
        }
    }

    /// ----------------------------------------------------------------------------------------------------------------

    public void addToWishList() {
        Product theProduct = cusView.obrLvProducts.getSelectionModel().getSelectedItem();
        CustomerUser currentUser = cusModel.getCurrentUser();

        if(theProduct!= null){

            ArrayList<Product> currentUserWishList = currentUser.getWishList();
            cusModel.makeOrganisedWishList(theProduct, currentUserWishList);

            cusModel.displayTaWishList = ProductListFormatter.buildString(cusModel.getWishList()); // build a String for trolley so that we can show it

            System.out.println("Current WishList:");
            for (Product product : currentUserWishList) {
                System.out.println("- " + product);
            }
        }
        else{
            cusModel.displayLaSearchResult = "Please search for an available product before adding it to the wish list";
            System.out.println("must search and get an available product before add to wish list");
        }
        cusModel.displayTaReceipt="";
        cusModel.updateView();
    }

    /// ----------------------------------------------------------------------------------------------------------------



    /// ----------------------------------------------------------------------------------------------------------------

    public void showHistory() {
        List<String> purchase = currentUser.getPurchaseHistory();
        if(purchase.isEmpty()) {
            String historyMsg = "No purchase has been done before";
            cusView.taHistory.setText(historyMsg);
        } else {
            String historyPurchase = String.join(",", purchase);
            cusView.taHistory.setText(historyPurchase);
        }
    }

    /// ----------------------------------------------------------------------------------------------------------------

    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Create Account" :
                handleCreateAccount(cusView.tfCreateAccID.getText(),
                        cusView.pfCreateAccPwd.getText(),
                        cusView.tfCreateAccUserFN.getText(),
                        cusView.tfCreateAccUserLN.getText(),
                        cusView.pfCreateAccPwd2.getText(),
                        cusView.tfCreateAccEmail.getText(),
                        cusView.dpCreateAccBDay.getValue());
                break;
            case "Search":
                cusModel.search();
                break;
            case "More Info":
                cusModel.checkInfo();
                break;
            case "My History":
                showHistory();
                break;
            case "Log Out":
                cusModel.setCurrentUser(null);
                System.out.println("User successfully Logged out");
                break;
            case "Add to Wish List":
                addToWishList();
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



}
