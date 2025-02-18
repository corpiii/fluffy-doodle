package com.clean_light.server.dummy;

import com.clean_light.server.product.domain.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductDummy {
    private final List<Product> dummyList = new ArrayList<>();

    public List<Product> getDummyList() {
        for (int i = 1; i <= 12; i++) {
            String dummyString = "testProduct" + i;
            Product product = Product.builder()
                    .id(Integer.toUnsignedLong(i))
                    .name(dummyString)
                    .description(dummyString)
                    .build();
            dummyList.add(product);
        }

        return List.copyOf(dummyList);
    }
}
