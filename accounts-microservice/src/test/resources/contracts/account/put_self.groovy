package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "put_cash_self_success"

    request {
        method 'POST'
        urlPath ('/accounts/put') {
            queryParameters {
                parameter 'currency': 'RUB'
                parameter 'amount': 100.0
            }
        }
        headers {
            header('Authorization', 'Bearer 123')
        }
    }

    response {
        status 200
    }
}
