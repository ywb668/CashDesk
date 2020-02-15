select o1.id            id,
       o1.account_id    account_id,
       o1.account_name  account_name,
       o1.create_time   create_time,
       o1.finish_time   finish_time,
       o1.actual_amount actual_amount,
       o1.total_money   total_money,
       o1.order_status  order_status,
       o2.order_id,
       o2.goods_id,
       o2.goods_name,
       o2.goods_introduce,
       o2.goods_num,
       o2.goods_unit,
       o2.goods_price,
       o2.goods_discount
    from `order` o1
         left join order_item o2 on o1.id=o2.order_id
    where account_id=? order by o1.id asc;

