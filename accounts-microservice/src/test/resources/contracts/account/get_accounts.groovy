package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "get_accounts"

    request {
        method 'GET'
        url '/accounts'
        headers {
            header('Authorization', 'Bearer 123')
        }
    }

    response {
        status 200
        body([
                [
                        exists  : true,
                        currency: "USD",
                        balance : 1500.0
                ],
                [
                        exists  : false,
                        currency: "RUB",
                        balance : 0.0
                ],
                [
                        exists  : false,
                        currency: "EUR",
                        balance : 0.0
                ]
        ])
        headers {
            contentType(applicationJson())
        }
    }
}
