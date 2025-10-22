package contracts.transfer

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    name 'successful_transfer'

    description '''
        Successful transfer request:
        - When valid request with different currencies and non-empty login is sent
        - Then status 200 OK is returned
    '''

    request {
        method POST()
        url("/transfer")
        body(
                from_currency: "EUR",
                to_currency  : "USD",
                amount      : 100.0,
                login       : "receiver"
        )
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status OK()
    }
}
