package ru.example.localetool.logic;

import ru.example.localetool.model.DataModel;

public class BusinessLogic {
    private DataModel data;

    public BusinessLogic() {
        data = new DataModel();
    }

    public DataModel getData() {
        return data;
    }
}
