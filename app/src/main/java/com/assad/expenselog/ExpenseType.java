package com.assad.expenselog;

public class ExpenseType {
     int expTypeId;
     String expTypeName;

    public ExpenseType() {

    }

    // Constructor
    public ExpenseType(int expTypeId, String expTypeName) {
        this.expTypeId = expTypeId;
        this.expTypeName = expTypeName;
    }

    // Getter for EXP_TYPE_ID
    public int getExpTypeId() {
        return expTypeId;
    }

    // Setter for EXP_TYPE_ID
    public void setExpTypeId(int expTypeId) {
        this.expTypeId = expTypeId;
    }

    // Getter for EXP_TYPE_NAME
    public String getExpTypeName() {
        return expTypeName;
    }

    // Setter for EXP_TYPE_NAME
    public void setExpTypeName(String expTypeName) {
        this.expTypeName = expTypeName;
    }

    public String toString() {
        return expTypeName;
    }

}



