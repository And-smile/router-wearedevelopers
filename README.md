# router-wearedevelopers
Router example for WeAreDevelopers conference presentation

### Description
The project shows 4 different approaches of routing traffic to newly extracted service.

#### Simple router:
>
> 100% of traffic is routed to new service

#### Incremental routing:
>
> step by step increasing amount of traffic forwarded to new service

#### Routing with fallback
>
> it can be one of previous approaches with additional fallback in case of error response from new service.
> Meaning that in case of error request will be processed by origin service(monolith)

#### Parallel processing
> meaning that all requests are being proce