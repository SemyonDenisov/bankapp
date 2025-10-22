package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should accept a list of currency quotations and return 200 OK"

    request {
        method POST()
        url("/update-quotations")
        headers {
            contentType(applicationJson())
        }
        body([
                [
                        currency: "USD",
                        rate    : 75.34
                ],
                [
                        currency: "EUR",
                        rate    : 89.12
                ]
        ])
    }

    response {
        status OK()
    }
}
