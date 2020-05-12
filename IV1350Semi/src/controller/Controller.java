package controller;

import dbHandler.*;
import model.*;
import DTO.*;
import model.sale.Sale;

/**
 * View calls all model classes through here.
 */
public class Controller {
    private ItemRegistry itemReg;
    private Sale sale;
    private CustomerRegistry customerReg;
    private CashRegister cashReg;
    private Receipt receipt;

    /**starts a new sale*/
    public void startNewSale() throws Exception {
        sale = new Sale();
        receipt = new Receipt();
    }

    /**
     * Creates a new instance.
     * @registryCreator is used for all operation calls.
     **/
    public Controller(RegistryCreator regCreator) {
        this.customerReg = regCreator.getCustomerRegistry();
        this.cashReg = new CashRegister(regCreator.getAccountingRegistry());
        this.itemReg = regCreator.getItemRegistry();
    }

    /**
     * method to get sale to add item, and
     * @param itemIdentifier is used to identify the item
     * @param itemQuantity shows how many of the item is to be added
     * @return the item as a ItemDTO
     */
    public ItemDTO callAddItem(int itemIdentifier, int itemQuantity) {
        ItemDTO item = itemReg.getItemSpecifications(itemIdentifier, itemQuantity);
            sale.addItem(item, itemQuantity);
            return item;
    }

    /**
     *  Gets customers discount and updates the price according to it
     * @param customerID is used to find the customer and therefor their discount
     */
    private void checksForDiscount(String customerID) {
        double discount = customerReg.getCustomerDiscount(customerID);
        sale.updateTotalPrice(discount);
    }

    /**
     * Starts the payment by checking discount, updates accounting registry and updates the inventory (though Sale)
     * @param customerID is used to identify costumer when checking for discount
     * @throws Exception
     */
    public void startPayment(String customerID) {
        checksForDiscount(customerID);
        cashReg.accountingUpdate(sale.getTotalPrice());
        sale.inventoryUpdate();
    }

    /**
     *  returns total price to pay
     */
    public double totalPrice(){
        return sale.getTotalPrice();
    }

    /**
     * Handles the payment logic, taking the money into Sale, and calls the printReceipt method to make the receipt
     * @param cash is the amount of money that is payed
     * @return the change
     * @throws Exception
     *
     * TODO from Sale.payment if cash is negative (not enough money paid)
     */
    public double payment(double cash){
        double change = sale.payment(cash);
        receipt.printReceipt(sale);
        return change;
    }

}