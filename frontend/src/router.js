import Vue from "vue";
import Router from "vue-router";
import Home from "@/pages/Home";
import PaymentPlanStart from "@/pages/PaymentPlanStart";
import CaliforniaPaymentPlanStart from "@/pages/CaliforniaPaymentPlanStart";
import StatePaymentPlan from "@/pages/StatePaymentPlan";
import NewJerseyPaymentPlanStart from "@/pages/NewJerseyPaymentPlanStart";
import GeorgiaPaymentPlanStart from "@/pages/GeorgiaPaymentPlanStart";
import MichiganPaymentPlanStart from "@/pages/MichiganPaymentPlanStart";
import PaymentPlanOrderForm from "@/pages/PaymentPlanOrderForm";
import CaliforniaPaymentPlanOrderForm from "@/pages/CaliforniaPaymentPlanOrderForm";
import NewJerseyPaymentPlanOrderForm from "@/pages/NewJerseyPaymentPlanOrderForm";
import GeorgiaPaymentPlanOrderForm from "@/pages/GeorgiaPaymentPlanOrderForm";
import MichiganPaymentPlanOrderForm from "@/pages/MichiganPaymentPlanOrderForm";
import OrderConfirmation from "@/pages/OrderConfirmation";
import OrderConfirmationPenaltyWaiver from "@/pages/OrderConfirmationPenaltyWaiver";
import TaxLienStart from "@/pages/TaxLienStart";
import TaxLienOrderForm from "@/pages/TaxLienOrderForm";
import OicLandingPage from "@/pages/OicLandingPage";
import OicCertification from "@/pages/OicCertification";
import OicIncomeAndExpenses from "@/pages/OicIncomeAndExpenses";
import OicAssetsPage from "@/pages/OicAssetsPage";
import OicSettlementOptions from "@/pages/OicSettlementOptions";
import OicApplicationInfo from "@/pages/OicApplicationInfo";
import OicOrderForm from "@/pages/OicOrderForm";
import RegisterUser from "@/pages/RegisterUser";
import MailRoomPortal from "@/pages/MailRoomPortal";
import MailRoom from "@/pages/MailRoom";
import OrderSearch from "@/pages/OrderSearch";
import OrderDetails from "@/pages/OrderDetails";
import Login from "@/pages/Login";
import PasswordReset from "@/pages/PasswordReset";
import PasswordResetConfirm from "@/pages/PasswordResetConfirm";
import Faqs from "@/pages/Faqs";
import ContactUs from "@/pages/ContactUs";
import AboutUs from "@/pages/AboutUs";
import Cancel from "@/pages/Cancel";
import PrivacyPolicy from "@/pages/PrivacyPolicy";
import Services from "@/pages/Services";
import TermsOfService from "@/pages/TermsOfService";
import CaseTrackerPage from "@/pages/CaseTrackerPage";
import { SiteUtils } from "./scripts/site-common";
import PenaltyOrderDetails from '@/pages/PenaltyOrderDetails';

Vue.use(Router);

let router = new Router({
  mode: "history",
  scrollBehavior() {
    return { x: 0, y: 0 };
  },
  routes: [
    {
      path: "/",
      name: "Home",
      component: Home,
      meta: {
        title: "97tax - Fast Tax Help at 97tax.com",
        metaTags: [
          {
            name: "description",
            content:
              "Tax relief specialists focused on affordable IRS payment plans and IRS tax lien removal options. Check if you qualify."
          }
        ]
      }
    },
    {
      path: "/payment-plan",
      name: "Payment Plans",
      component: PaymentPlanStart,
      meta: {
        title: "IRS Payment Plan Application Help",
        metaTags: [
          {
            name: "description",
            content:
              "Order an IRS payment plan application in minutes. Flat fee of $97."
          }
        ]
      }
    },
    {
      path: "/state-payment-plan",
      name: "State Payment Plans",
      component: StatePaymentPlan,
      meta: {
        title: "State Payment Plan",
        metaTags: [
          {
            name: "state-payment-plan",
            content:
              "Order an state payment plan application in minutes. Flat fee of $97."
          }
        ]
      }
    },
    {
      path: "/california-payment-plan",
      name: "California Payment Plans",
      component: CaliforniaPaymentPlanStart,
      meta: {
        title: "IRS California Payment Plan Application Help",
        metaTags: [
          {
            name: "description",
            content:
              "Order an IRS california payment plan application in minutes. Flat fee of $97."
          }
        ]
      }
    },
    {
      path: "/new-jersey-payment-plan",
      name: "New Jersey Payment Plans",
      component: NewJerseyPaymentPlanStart,
      meta: {
        title: "New Jersey Payment Plan Application Help",
        metaTags: [
          {
            name: "description",
            content:
              "Order New Jersey payment plan application in minutes. Flat fee of $97."
          }
        ]
      }
    },
    {
      path: "/georgia-payment-plan",
      name: "Georgia Payment Plans",
      component: GeorgiaPaymentPlanStart,
      meta: {
        title: "IRS Georgia Payment Plan Application Help",
        metaTags: [
          {
            name: "description",
            content:
              "Order an IRS georgia payment plan application in minutes. Flat fee of $97."
          }
        ]
      }
    },
    {
      path: "/michigan-payment-plan",
      name: "Michigan Payment Plans",
      component: MichiganPaymentPlanStart,
      meta: {
        title: "IRS Michigan Payment Plan Application Help",
        metaTags: [
          {
            name: "description",
            content:
              "Order an IRS Michigan payment plan application in minutes. Flat fee of $97."
          }
        ]
      }
    },
    {
      path: "/georgia-payment-plan-order-form",
      name: "Georgia Order Form",
      component: GeorgiaPaymentPlanOrderForm,
      meta: {
        title: "Georgia Payment Plan Application Help"
      }
    },
    {
      path: "/michigan-payment-plan-order-form",
      name: "Michigan Order Form",
      component: MichiganPaymentPlanOrderForm,
      meta: {
        title: "Michigan Payment Plan Application Help"
      }
    },
    {
      path: "/payment-plan-order-form",
      name: "Order Form",
      component: PaymentPlanOrderForm,
      meta: {
        title: "IRS Payment Plan Application Help"
      }
    },
    {
      path: "/california-payment-plan-order-form",
      name: "California Order Form",
      component: CaliforniaPaymentPlanOrderForm,
      meta: {
        title: "California Payment Plan Application Help"
      }
    },
    {
      path: "/new-jersey-payment-plan-order-form",
      name: "New Jersey Order Form",
      component: NewJerseyPaymentPlanOrderForm,
      meta: {
        title: "New Jersey Payment Plan Application Help"
      }
    },
    {
      path: "/order-confirmation/:correlationId",
      name: "Order Confirmation",
      component: OrderConfirmation,
      meta: {
        title: "Order Successful - 97tax"
      }
    },
    {
      path: "/order-confirmation/penalty-waiver/:correlationId",
      name: "Order Confirmation",
      component: OrderConfirmationPenaltyWaiver,
      meta: {
        title: "Order Successful - 97tax"
      }
    },
    {
      path: "/tax-liens",
      name: "Tax Liens",
      component: TaxLienStart,
      meta: {
        title: "Federal Tax Lien Removal Help - 97tax",
        metaTags: [
          {
            name: "description",
            content:
              "Order an IRS tax lien removal application in minutes. Flat fee of $97."
          }
        ]
      }
    },
    {
      path: "/tax-lien-order-form",
      name: "Tax Lien Order Form",
      component: TaxLienOrderForm,
      meta: {
        title: "Federal Tax Lien Removal - 97tax"
      }
    },
    // {
    //   path: '/offer-in-compromise',
    //   name: 'Offer In Compromise',
    //   component: OicLandingPage
    // },
    // {
    //   path: '/oic-certification',
    //   name: 'Offer In Compromise Certification',
    //   component: OicCertification
    // },
    // {
    //   path: '/oic-income-and-expenses',
    //   name: 'Offer In Compromise Income and Expenses',
    //   component: OicIncomeAndExpenses
    // },
    // {
    //   path: '/oic-assets',
    //   name: 'Offer In Compromise Assets',
    //   component: OicAssetsPage
    // },
    // {
    //   path: '/oic-settlement',
    //   name: 'Offer In Compromise Settlement Options',
    //   component: OicSettlementOptions
    // },
    // {
    //   path: '/oic-additional-info',
    //   name: 'Application Info',
    //   component: OicApplicationInfo
    // },
    // {
    //   path: '/oic-order-form',
    //   name: 'Order Form',
    //   component: OicOrderForm
    // },
    {
      path: "/mailroom",
      name: "MailRoom",
      component: MailRoomPortal
    },
    {
      path: "/admin/mailroom",
      name: "MailRoom",
      component: MailRoom
    },
    {
      path: "/admin/register",
      name: "Register User",
      component: RegisterUser
    },
    {
      path: "/admin/orders",
      name: "Order History",
      component: OrderSearch
    },
    {
      path: "/admin/order/:id",
      name: "Order Details",
      component: OrderDetails
    },
    {
      path: "/admin/penalty-order/:id",
      name: "Penalty Order Details",
      component: PenaltyOrderDetails
    },
    {
      path: "/login",
      name: "Login",
      component: Login
    },
    {
      path: "/password-reset",
      name: "Password Reset",
      component: PasswordReset
    },
    {
      path: "/password-reset/:id",
      name: "Password Reset Confirmation",
      component: PasswordResetConfirm
    },
    {
      path: "/faqs",
      name: "FAQs",
      component: Faqs,
      meta: {
        title: "FAQs - 97tax",
        metaTags: [
          {
            name: "description",
            content: "Frequently asked questions - 97tax"
          }
        ]
      }
    },
    {
      path: "/contact-us",
      name: "ContactUs",
      component: ContactUs,
      meta: {
        title: "Contact Us - 97tax",
        metaTags: [
          {
            name: "description",
            content: "Contact us by phone or email."
          }
        ]
      }
    },
    {
      path: "/about-us",
      name: "AboutUs",
      component: AboutUs,
      meta: {
        title: "About Us - 97tax",
        metaTags: [
          {
            name: "description",
            content: "About Us"
          }
        ]
      }
    },
    {
      path: "/cancel",
      name: "Cancel",
      component: Cancel
    },
    {
      path: "/privacy-policy",
      name: "PrivacyPolicy",
      component: PrivacyPolicy,
      meta: {
        title: "Privacy Policy - 97tax"
      }
    },
    {
      path: "/services",
      name: "Services",
      component: Services,
      meta: {
        title: "Services Offered - 97tax"
      }
    },
    {
      path: "/terms-of-service",
      name: "TermsOfService",
      component: TermsOfService,
      meta: {
        title: "Terms of Service - 97tax"
      }
    },
    {
      path: "/case-tracker",
      name: "CaseTracker",
      component: Home,
      meta: {
        title: "97tax - Fast Tax Help at 97tax.com",
        metaTags: [
          {
            name: "description",
            content:
              "Tax relief specialists focused on affordable IRS payment plans and IRS tax lien removal options. Check if you qualify."
          }
        ]
      }
    },
    { path: "*", redirect: "/" }
  ]
});

// This callback runs before every route change, including on page load.
router.beforeEach((to, from, next) => {
  // This goes through the matched routes from last to first, finding the closest route with a title.
  // eg. if we have /some/deep/nested/route and /some, /deep, and /nested have titles, nested's will be chosen.
  const nearestWithTitle = to.matched
    .slice()
    .reverse()
    .find(r => r.meta && r.meta.title);

  // Find the nearest route element with meta tags.
  const nearestWithMeta = to.matched
    .slice()
    .reverse()
    .find(r => r.meta && r.meta.metaTags);
  const previousNearestWithMeta = from.matched
    .slice()
    .reverse()
    .find(r => r.meta && r.meta.metaTags);

  // If a route with a title was found, set the document (page) title to that value.
  if (nearestWithTitle) document.title = nearestWithTitle.meta.title;

  // Remove any stale meta tags from the document using the key attribute we set below.
  Array.from(document.querySelectorAll("[data-vue-router-controlled]")).map(
    el => el.parentNode.removeChild(el)
  );

  // Skip rendering meta tags if there are none.
  if (!nearestWithMeta) return next();

  // Turn the meta tag definitions into actual elements in the head.
  nearestWithMeta.meta.metaTags
    .map(tagDef => {
      const tag = document.createElement("meta");

      Object.keys(tagDef).forEach(key => {
        tag.setAttribute(key, tagDef[key]);
      });

      // We use this to track which meta tags we create, so we don't interfere with other ones.
      tag.setAttribute("data-vue-router-controlled", "");

      return tag;
    })
    // Add the meta tags to the document head.
    .forEach(tag => document.head.appendChild(tag));

  next();
});

export default router;
