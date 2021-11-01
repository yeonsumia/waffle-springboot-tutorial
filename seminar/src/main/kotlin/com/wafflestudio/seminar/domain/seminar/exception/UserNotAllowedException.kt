package com.wafflestudio.seminar.domain.seminar.exception

import com.wafflestudio.seminar.global.common.exception.NotAllowedException
import com.wafflestudio.seminar.global.common.exception.ErrorType


class UserNotAllowedException(detail: String="") :
    NotAllowedException(ErrorType.INVALID_REQUEST, detail)