# Implementation Alignment

## BadgeClass

| Property | Field | Type | Description |
| ------------------- | ----------- | ---- | ----------- |
| id | linearId | `UniqueIdentifier` | identifier of the badge type|
| type | NA | NA | NA |
| name | name | `String` | name of the achievement |
| description | description | `String` | a short description of the achievement |
| image | TBI | TBI | IRI or document representing an image of the achievement. This must be a PNG or SVG image. |
| criteria | TBI | TBI | URI or embedded criteria document describing how to earn the achievement |
| issuer | issuer | `Party` | parties that issue the badge |
| alignment | TBI | TBI | An object describing which objectives or educational standards this badge aligns to, if any |
| tags | TBI | TBI | A tag that describes the type of achievement |

**TBI = To Be Implemented*

**NA = Not Applicable*

## Assertion

| Property | Field | Type | Description |
| -------- | ----- | ---- | ----------- |
| id | linearId | `UniqueIdentifier` | identifier of this assertion |
| type | NA | NA | NA |
| recipient | recipient | `AbstractParty` | recipient of this achievement |
| badge | badge | `BadgeClass` | IRI or document that describes the type of badge being awarded |
| verification | TBI | TBI | instructions for third parties to verify this assertion |
| issuedOn | TBI | TBI | Timestamp of when the achievement was awarded |
| image | TBI | TBI | IRI or document representing an image representing this user’s achievement |
| evidence | TBI | TBI | IRI or document describing the work that the recipient did to earn the achievement |
| narrative | TBI | TBI | A narrative that connects multiple pieces of evidence |
| expires | TBI | TBI | If the achievement has some notion of expiry, this indicates a timestamp when a badge should no longer be considered valid. After this time, the badge should be considered expired |
| revoked | TBI | TBI | whether the assertion is valid |
| revocationReason | TBI | TBI | Optional published reason for revocation, if revoked |

## Workflows

| Open Badge | Token SDK | Description |
| ---------- | --------- | ----------- |
| Create Badge Classes | `CreateBadgeClass` calling `CreateEvolvableToken` subflow | An issuer can *create* a BadgeClass |
| Issue Assertions | `IssueAssertion` calling `IssueTokens` subflow | An issuer can  *issue* Assertions based on a BadgeClass |

Additional workflows for tasks like update BadgeClasses, revoking Assertions, validating Assertions will be added at a later version.

