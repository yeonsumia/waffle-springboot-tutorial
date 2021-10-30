package com.wafflestudio.seminar.domain.seminar.exception

import com.wafflestudio.seminar.global.common.exception.InvalidRequestException
import com.wafflestudio.seminar.global.common.exception.ErrorType


class RequestInvalidFormException(detail: String="") :
        InvalidRequestException(ErrorType.INVALID_REQUEST, detail)