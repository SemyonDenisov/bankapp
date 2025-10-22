package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "put_cash_another_success"

    request {
        method 'POST'
        urlPath('/accounts/put') {
            queryParameters {
                parameter 'currency': 'USD'
                parameter 'amount'  : 500.0
                parameter 'login'   : 'user@example.com'
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
