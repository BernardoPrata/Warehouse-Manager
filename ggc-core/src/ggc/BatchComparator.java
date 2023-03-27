package ggc;

import java.util.Comparator;

public class BatchComparator implements Comparator<Batch> {
    public int compare(Batch batch1, Batch batch2) {
        CollatorWrapper wrapper = new CollatorWrapper();
        int i = 0;
        /**
         * sort by Product Ids:
         */
        i += wrapper.compare(batch1.getProductId(), batch2.getProductId());

        /**
         * the ProductIds are equal, sort by PartnerIds:
         */
        if (i == 0)
            i += wrapper.compare(batch1.getPartnerId(), batch2.getPartnerId());

        /**
         * the ProductIds and PartnerIds are equal, sort by price:
         */
        if (i == 0)
            i += batch1.getPrice() - batch2.getPrice();

        /**
         * the ProductIds ,PartnerIds and price are equal, sort by stock:
         */
        if (i == 0)
            i += batch1.getStock() - batch2.getStock();

        return i;
    }
}