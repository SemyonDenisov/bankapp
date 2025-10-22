
package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return list of currency quotations"

    request {
        method GET()
        url("/rates")
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                [
                        currency: $(consumer(anyOf("USD", "EUR", "RUB")), producer("USD")),
                        rate    : $(consumer(anyDouble()), producer(96.0))
                ],
                [
                        currency: $(consumer(anyOf("USD", "EUR", "RUB")), producer("EUR")),
                        rate    : $(consumer(anyDouble()), producer(96.0))
                ],
                [
                        currency: $(consumer(anyOf("USD", "EUR", "RUB")), producer("EUR")),
                        rate    : $(consumer(anyDouble()), producer(1.0))
                ]
        ])
    }
}
