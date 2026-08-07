# End User License Agreement

This End User License Agreement (the "Agreement") is a legal agreement between
iPlateia ("iPlateia", "we", "us") and the individual or legal entity that
downloads, installs, accesses, or uses the SORI SDK or the SORI API (the
"Licensee", "you"). By downloading, installing, integrating, or otherwise using
the Licensed Materials defined below, you accept this Agreement. If you do not
accept it, do not use the Licensed Materials.

> [!WARNING]
> This page states the standard terms for use of the SORI SDK and the SORI API.
> Where you have signed a separate written commercial agreement with iPlateia,
> that agreement prevails over this page to the extent of any conflict. This page
> is published for reference and does not replace an executed commercial
> agreement.

## 1. Definitions

- **Licensed Materials** — the SORI SDK distributions for supported platforms,
  the SORI API, the SORI Console, and the accompanying documentation, sample
  code, and Credentials made available by iPlateia.
- **SORI SDK** — the client libraries and binary artifacts distributed by
  iPlateia for integration into your applications.
- **SORI API** — the campaign delivery, recognition-event reporting, and
  management interfaces operated by iPlateia.
- **Application** — your own software product into which you integrate the SORI
  SDK, registered under your SORI Console account.
- **Credentials** — the App ID, secret key, tokens, and any other authentication
  material issued to you through the SORI Console.
- **End User** — a person who uses your Application.
- **Recognition Data** — signature identifiers, recognition results, campaign
  responses, and related statistics produced by the Licensed Materials.

## 2. What iPlateia provides

Subject to your compliance with this Agreement and with any applicable
commercial agreement, iPlateia provides:

- **SDK distributions.** Platform SDK artifacts for the platforms published in
  this documentation, together with installation and implementation guides and
  release notes.
- **Recognition and campaign service.** On-device audio recognition through the
  SORI SDK, with campaign delivery and reporting through the SORI API, within the
  operational limits of your plan or commercial agreement.
- **SORI Console access.** An account through which you manage materials, audio
  markers, campaigns, members, applications, and Credentials. You operate your
  Console account independently; iPlateia does not use it to run your service on
  your behalf.
- **Documentation and support.** This public technical documentation and the
  support channels described in your plan or commercial agreement. Support scope,
  response times, and service levels are not established by this page.

## 3. Grant of license

iPlateia grants you a limited, non-exclusive, non-transferable,
non-sublicensable, revocable license, for the term of this Agreement, to:

1. install and use the SORI SDK to develop, test, and operate your own
   Applications;
2. reproduce and distribute the SORI SDK **only** as an integrated,
   non-separable component of your Applications, in object or binary form;
3. use the SORI API and the SORI Console to operate those Applications; and
4. use the documentation and sample code internally for the purposes above.

Sample code published in this documentation may be copied and modified within
your Applications. All rights not expressly granted are reserved by iPlateia.

## 4. Your obligations

- **Registration.** Use the Licensed Materials only with Applications registered
  under your SORI Console account, using Credentials issued to you.
- **Credential protection.** Keep your secret key confidential, do not publish it
  or embed it where it can be trivially extracted, do not share it across
  organizations, and revoke and reissue it promptly if it is exposed.
- **End User disclosure and consent.** Your Application captures audio through
  the device microphone. You are responsible for obtaining the platform
  permissions and any consent required by applicable law, and for disclosing in
  your own privacy notice that audio is processed for content recognition. Where
  recognition can continue while your Application is not in the foreground, you
  must give End Users a clear way to stop it.
- **Content rights.** You are responsible for holding the rights necessary to
  register and use your materials, audio markers, campaign assets, and metadata.
- **Legal and platform compliance.** Comply with applicable laws, including data
  protection law, and with the rules of the app stores and platforms through
  which you distribute your Applications.
- **Notices.** Retain all copyright, trademark, and proprietary notices included
  in the Licensed Materials.

## 5. Prohibited conduct

You must not, and must not permit any third party to:

1. **Reverse engineer.** Decompile, disassemble, deobfuscate, extract embedded
   models or reference datasets, or otherwise attempt to derive the source code,
   algorithms, fingerprinting method, protocol internals, or structure of the
   Licensed Materials, except to the extent this restriction is unenforceable
   under applicable law.
2. **Modify or derive.** Alter, adapt, translate, patch, repackage, or create
   derivative works of the SORI SDK, or access its internals other than through
   its documented public interfaces.
3. **Redistribute.** Sell, sublicense, lease, rent, lend, host, or otherwise make
   the SORI SDK or the SORI API available to third parties as a standalone
   component, wrapper, proxy, reseller offering, or competing service.
4. **Circumvent controls.** Bypass or tamper with authentication, license
   validation, rate limits, quotas, usage metering, or any technical protection
   measure; use Credentials issued to another party; or disclose Credentials to
   unauthorized parties.
5. **Abuse the service.** Generate artificial, automated, replayed, spoofed, or
   otherwise fabricated recognition events, impressions, clicks, or reports;
   inflate or distort statistics; run load, stress, penetration, or scraping
   activity against the SORI API without prior written authorization; or place an
   unreasonable load on the service.
6. **Build a competing dataset.** Use the Licensed Materials or Recognition Data
   to develop, train, publicly benchmark, or improve any competing audio
   recognition, fingerprinting, or watermarking technology, or to accumulate a
   fingerprint or reference dataset outside the service.
7. **Misuse data.** Attempt to re-identify individuals from Recognition Data,
   combine it with other data to profile End Users in violation of applicable
   law, or use the Licensed Materials for covert surveillance, eavesdropping, or
   any capture that End Users have not been informed of.
8. **Use unlawfully.** Use the Licensed Materials for any unlawful, deceptive,
   infringing, or harmful purpose, or in a way that damages iPlateia, its
   licensors, or any third party.
9. **Misrepresent.** Imply endorsement, certification, or partnership by iPlateia
   beyond what is granted in writing, or use iPlateia trademarks without
   permission.

## 6. Disclaimer of warranties

THE LICENSED MATERIALS ARE PROVIDED "AS IS" AND "AS AVAILABLE", WITHOUT WARRANTY
OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
NON-INFRINGEMENT, AND ANY WARRANTY ARISING FROM COURSE OF DEALING OR USAGE OF
TRADE.

Without limiting the above, iPlateia does not warrant that:

- recognition will succeed, or will succeed within any particular time, for a
  given audio material, playback volume, distance, ambient noise level, or device
  microphone;
- recognition results, campaign delivery, or reports will be complete,
  uninterrupted, error-free, or free of false or missed matches;
- the SORI API will be available without interruption, or that maintenance,
  degradation, and outages will not occur;
- operating-system audio capture behavior, background-execution policy,
  permission models, or third-party platform and store policies will remain
  unchanged, or that the Licensed Materials will be unaffected by such changes;
- the Licensed Materials will be compatible with every device, OS version, audio
  configuration, or other SDK present in your Application; or
- the Licensed Materials are suitable for safety-critical, medical, emergency,
  legally evidentiary, or life-sustaining use.

> [!TIP]
> Platform-level limitations that are already known and documented are listed in
> [Known Caveats](https://docs.soriapi.com/integration/caveats). Reviewing that page is part of a correct
> integration.

## 7. Ownership

The Licensed Materials are licensed, not sold. iPlateia and its licensors retain
all rights, title, and interest in and to the Licensed Materials, including all
intellectual property rights, the recognition algorithms, the fingerprint and
signature formats, and all improvements to them. You retain all rights in your
Applications and in the content you register, excluding the Licensed Materials
embedded in them. Feedback you choose to send us may be used to improve the
Licensed Materials without obligation to you.

## 8. Fees, plans, and usage limits

Access may be subject to fees, quotas, and operational limits set out in your
plan or commercial agreement. iPlateia may apply reasonable technical limits to
protect service stability. Where you expect a significant short-term increase in
traffic, notify iPlateia in advance as described in the [FAQ](https://docs.soriapi.com/faq) so that
capacity can be prepared.

## 9. Changes to the Licensed Materials

iPlateia may update, improve, or discontinue parts of the Licensed Materials,
including SDK interfaces and API behavior. Material breaking changes will be
communicated through release notes or direct notice where practicable. You are
responsible for keeping your integration on a supported version.

## 10. Term and termination

This Agreement takes effect when you first use the Licensed Materials and
continues until terminated. You may terminate it at any time by ceasing all use
and removing the SORI SDK from your Applications. iPlateia may suspend access or
terminate this Agreement immediately if you breach it, if suspension is required
to protect the service or to comply with law, or as provided in your commercial
agreement.

On termination you must stop all use of the Licensed Materials, remove the SORI
SDK from Applications you distribute after that date, and delete or destroy
copies of the Licensed Materials and Credentials in your control. Sections 5, 6,
7, 11, 12, and 13 survive termination.

## 11. Limitation of liability

TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW, IPLATEIA SHALL NOT BE LIABLE
FOR ANY INDIRECT, INCIDENTAL, SPECIAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES, OR
FOR ANY LOSS OF PROFITS, REVENUE, DATA, GOODWILL, OR BUSINESS INTERRUPTION,
ARISING OUT OF OR RELATING TO THE LICENSED MATERIALS OR THIS AGREEMENT, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

Where liability cannot be excluded, iPlateia's total aggregate liability is
limited to the amounts you paid to iPlateia for the Licensed Materials during the
twelve months preceding the event giving rise to the claim, or, where no fees
were paid, to the minimum amount permitted by applicable law. Nothing in this
Agreement excludes liability that cannot be excluded by law.

## 12. Indemnification

You will defend and indemnify iPlateia against third-party claims arising from
your Applications, the content and campaigns you register, your handling of End
User data, or your breach of this Agreement, except to the extent the claim
arises from iPlateia's own breach or unlawful conduct.

## 13. Confidentiality, export, and compliance

Non-public information about the Licensed Materials, including Credentials,
pre-release artifacts, and technical details not published in this documentation,
is confidential and must be protected with at least reasonable care. You must
comply with applicable export control and sanctions laws and must not make the
Licensed Materials available where doing so is prohibited.

## 14. Governing law and disputes

This Agreement is governed by the laws of the Republic of Korea, without regard
to its conflict of law rules. Disputes are subject to the exclusive jurisdiction
of the courts located in Seoul, Republic of Korea, unless your commercial
agreement provides otherwise.

## 15. Changes to this Agreement

iPlateia may revise this Agreement. Revisions take effect when they are published
on this page. Continued use of the Licensed Materials after publication
constitutes acceptance of the revised Agreement.

## 16. General

If any provision is held unenforceable, the remaining provisions stay in effect.
Failure to enforce a provision is not a waiver. You may not assign this Agreement
without iPlateia's prior written consent. This Agreement, together with any
applicable commercial agreement, is the entire agreement between the parties
regarding the Licensed Materials.

## 17. Contact

For questions about this Agreement, licensing scope, or authorization requests,
contact iPlateia at [help@iplateia.com](mailto:help@iplateia.com).
