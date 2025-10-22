package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "withdraw_success"

    request {
        method 'POST'
        urlPath('/withdraw') {
            queryParameters {
                parameter 'currency': 'USD'
                parameter 'amount': 200.0
            }
        }
    }

    response {
        status 200
    }
}
