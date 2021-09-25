package com.wafflestudio.seminar.domain.user.exception

import com.wafflestudio.seminar.global.common.exception.NotAllowedException
import com.wafflestudio.seminar.global.common.exception.ErrorType


class UserNotInstructorException(detail: String="") :
        NotAllowedException(ErrorType.INVALID_REQUEST, detail)