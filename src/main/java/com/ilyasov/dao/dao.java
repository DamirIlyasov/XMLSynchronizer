package com.ilyasov.dao;

import com.ilyasov.model.Department;

import java.util.HashMap;

public interface dao {
    void update(HashMap<Integer, Department> depFromXml);

    HashMap<Integer, Department> read();
}
