package contracts

import org.springframework.cloud.contract.spec.Contract


Contract.make {
    description "Should convert currency from USD to EUR and return calculated amount"

    request {
        method GET()
        url("/conversion") {
            queryParameters {
                parameter("from", "USD")
                parameter("to", "EUR")
                parameter("amount", 100.0)
            }
        }
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
                from: "USD",
                to: "EUR",
                amount: anyDouble()
        )
    }
}
