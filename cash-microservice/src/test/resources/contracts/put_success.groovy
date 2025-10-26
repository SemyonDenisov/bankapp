package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "put_success"

    request {
        method 'POST'
        urlPath('/put') {
            queryParameters {
                parameter 'currency': 'USD'
                parameter 'amount': '500.0'
            }
        }
    }

    response {
        status 200
    }
}
