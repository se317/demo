package model

case class Order(exchange: String,
                 input: String,
                 amount: BigDecimal,
                 output: String
                )
