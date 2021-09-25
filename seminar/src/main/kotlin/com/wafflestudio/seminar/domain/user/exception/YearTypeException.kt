package com.wafflestudio.seminar.domain.user.exception

import com.wafflestudio.seminar.global.common.exception.InvalidRequestException
import com.wafflestudio.seminar.global.common.exception.ErrorType


class YearTypeException(detail: String="") :
        InvalidRequestException(ErrorType.INVALID_REQUEST, detail)