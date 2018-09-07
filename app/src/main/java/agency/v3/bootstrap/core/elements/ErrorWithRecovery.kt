package agency.v3.bootstrap.core.elements

/**
 * An class that encapsulates error details as well as options for recovering from it
 * @property reason reason of error, human readable
 * @property reasonThrowable error source
 * @property recovery action to recover from error, nullable
 * @property cancellation cancel action
 *
 */
class ErrorWithRecovery(
        val reasonThrowable: Throwable,
        val reason: String = reasonThrowable.message ?: "",
        val recovery: (() -> Unit)? = null,
        val cancellation: (() -> Unit)? = null
)