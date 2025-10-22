package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name "withdraw_cash_success"

    request {
        method 'POST'
        urlPath('/accounts/withdraw') {
            queryParameters {
                parameter 'currency': 'USD'
                parameter 'amount': 200.0
            }
        }
        headers {
            header('Authorization', value(regex('Bearer .+')))
        }
    }

    response {
        status 200
    }
}
